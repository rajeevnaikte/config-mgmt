package com.rajeevn.configmgmt.dao;

import com.rajeevn.common.interfaces.ThrowableCallback;
import com.rajeevn.common.util.PropertiesUtil;
import com.rajeevn.configmgmt.model.Config;
import com.rajeevn.configmgmt.model.RolePermissions;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static com.rajeevn.common.util.CollectionsUtil.isStartsWithAny;
import static com.rajeevn.common.util.CollectionsUtil.listToString;
import static com.rajeevn.common.util.CollectionsUtil.stringToList;
import static com.rajeevn.common.util.FileIOUtil.deleteQuietlyRecursively;
import static com.rajeevn.common.util.FileIOUtil.fileNameWithoutExt;
import static com.rajeevn.common.util.PropertiesUtil.addOrUpdate;
import static com.rajeevn.configmgmt.common.Util.versionComment;
import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;
import static org.eclipse.jgit.api.Git.cloneRepository;
import static org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE;

@Repository
public class GitConfigsRepository extends AbstractConfigsRepository
{
    private static final Logger LOG = LoggerFactory.getLogger(GitConfigsRepository.class);
    private static File REPO_DIR = new File("./repo");
    private static final String CONFIG_MGMT_BRANCH = "configmgmt";
    private static final String ROLES_FILE = "roles.properties";
    private static final String ROLES_DELIM = ",";

    private static final String ROLES_KEY = "config.mgmt.roles";
    private static final String READONLY_PERMITTED_ENVS_KEY = "config.mgmt.{0}.readonly.permitted.environments";
    private static final String WRITE_PERMITTED_ENVS_KEY = "config.mgmt.{0}.write.permitted.environments";
    private static final String READONLY_PERMITTED_KEYS_KEY = "config.mgmt.{0}.readonly.permitted.keyPrefixes";
    private static final String WRITE_PERMITTED_KEYS_KEY = "config.mgmt.{0}.write.permitted.keyPrefixes";
    private static final String NOT_PERMITTED_KEYS_KEY = "config.mgmt.{0}.not.permitted.keyPrefixes";

    private Map<String, Git> branchRepoMap = new HashMap<>();
    private Git configMgmt;

    @Value("${config.mgmt.git.url}")
    private String gitUrl;
    @Value("${config.mgmt.git.username}")
    private String userName;
    @Value("${config.mgmt.git.password}")
    private String password;
    @Value("${config.mgmt.git.email}")
    private String email;

    @Value("${config.mgmt.git.repo.dir:./repo}")
    public void setRepoDir(String repoDir)
    {
        REPO_DIR = new File(repoDir);
    }

    private String[] appendFileNameFor;

    @Value("${config.mgmt.git.appendFilePath.for.keyPrefixes:spring,server,security,logging,management}")
    public void setAppendFileNameFor(String value)
    {
        appendFileNameFor = ofNullable(value).orElse("").split(",");
    }

    @Override
    public Map<String, RolePermissions> loadRolesFromDataStore()
    {
        try
        {
            configMgmt.pull().call();
            final Properties roleProps = new Properties();
            roleProps.load(new FileInputStream(new File(configMgmt.getRepository().getDirectory().getParentFile(), ROLES_FILE)));
            return stringToList(roleProps.getProperty(ROLES_KEY), ROLES_DELIM).stream()
                    .collect(Collectors.toMap(role -> role.toLowerCase(), role ->
                    {
                        RolePermissions perm = new RolePermissions();
                        perm.setNotPermittedKeysPrefix(stringToList(roleProps.getProperty(format(NOT_PERMITTED_KEYS_KEY, role)), ROLES_DELIM));
                        perm.setReadOnlyEnvs(stringToList(roleProps.getProperty(format(READONLY_PERMITTED_ENVS_KEY, role)), ROLES_DELIM));
                        perm.setReadOnlyKeysPrefix(stringToList(roleProps.getProperty(format(READONLY_PERMITTED_KEYS_KEY, role)), ROLES_DELIM));
                        perm.setWriteEnvs(stringToList(roleProps.getProperty(format(WRITE_PERMITTED_ENVS_KEY, role)), ROLES_DELIM));
                        perm.setWriteKeysPrefix(stringToList(roleProps.getProperty(format(WRITE_PERMITTED_KEYS_KEY, role)), ROLES_DELIM));
                        return perm;
                    }));
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void createConfigMgmtBranch(Git temp) throws GitAPIException, IOException
    {
        RevCommit first = null;
        Iterator<RevCommit> log = temp.log().all().call().iterator();
        while (log.hasNext())
            first = log.next();
        temp.checkout().setCreateBranch(true).setStartPoint(first).setName(CONFIG_MGMT_BRANCH).call();
        File roles = new File(temp.getRepository().getDirectory().getParentFile(), ROLES_FILE);
        roles.createNewFile();
        temp.add().addFilepattern(ROLES_FILE).call();
        temp.commit().setCommitter(userName, email).setMessage(versionComment("Adding role file")).call();
        temp.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, password)).call();
        configMgmt = cloneRepoForBranch(CONFIG_MGMT_BRANCH);
    }

    private void changeAndPush(Git git, ThrowableCallback<Exception> makeChanges, String commitMsg) throws Exception
    {
        git.pull().call();
        makeChanges.call();
        git.commit().setCommitter(userName, email).setMessage(versionComment(commitMsg)).call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, password)).call();
    }

    @Override
    protected void saveRoleToDataStore(String role, RolePermissions permissions)
    {
        try
        {
            changeAndPush(configMgmt, () -> saveRoleToFile(role, permissions),
                    "Adding role " + role);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void closeAllRepo()
    {
        ofNullable(configMgmt).ifPresent(Git::close);
        configMgmt = null;
        branchRepoMap.forEach((s, git) -> ofNullable(git).ifPresent(Git::close));
        branchRepoMap.clear();
    }

    @Override
    protected void releaseResources()
    {
        closeAllRepo();
    }

    @Override
    public Set<String> loadEnvsFromDataStore()
    {
        try
        {
            closeAllRepo();
            deleteQuietlyRecursively(REPO_DIR);
            File temp = getDirForClone("temp");
            Git git = cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(temp)
                    .setCloneAllBranches(true)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, password))
                    .call();

            Set<String> envs = git.branchList()
                    .setListMode(REMOTE)
                    .call().stream()
                    .map(ref -> ref.getName().replace("refs/remotes/origin/", ""))
                    .collect(Collectors.toSet());

            if (envs.remove(CONFIG_MGMT_BRANCH))
                configMgmt = cloneRepoForBranch(CONFIG_MGMT_BRANCH);
            else
                createConfigMgmtBranch(git);

            git.close();
            deleteQuietlyRecursively(temp);
            return envs;
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void loadEnvFromDataStore(String branch)
    {
        try
        {
            if (branchRepoMap.get(branch) != null)
            {
                branchRepoMap.get(branch).close();
                deleteQuietlyRecursively(branchRepoMap.get(branch).getRepository().getDirectory().getParentFile());
            }

            branchRepoMap.put(branch, cloneRepoForBranch(branch));
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void saveRoleToFile(String role, RolePermissions permissions) throws Exception
    {
        File rolesFile = new File(configMgmt.getRepository().getDirectory().getParentFile(), ROLES_FILE);
        addOrUpdate(rolesFile, roleProps ->
        {
            String roles = roleProps.getProperty(ROLES_KEY);
            if (roles == null)
                roles = role;
            else if (Arrays.stream(roles.split(ROLES_DELIM)).noneMatch(r -> r.equalsIgnoreCase(role)))
                roles += ROLES_DELIM + role;
            roleProps.setProperty(ROLES_KEY, roles);
            roleProps.setProperty(format(READONLY_PERMITTED_ENVS_KEY, role),
                    listToString(permissions.getReadOnlyEnvs(), ROLES_DELIM));
            roleProps.setProperty(format(WRITE_PERMITTED_ENVS_KEY, role),
                    listToString(permissions.getWriteEnvs(), ROLES_DELIM));
            roleProps.setProperty(format(READONLY_PERMITTED_KEYS_KEY, role),
                    listToString(permissions.getReadOnlyKeysPrefix(), ROLES_DELIM));
            roleProps.setProperty(format(WRITE_PERMITTED_KEYS_KEY, role),
                    listToString(permissions.getWriteKeysPrefix(), ROLES_DELIM));
            roleProps.setProperty(format(NOT_PERMITTED_KEYS_KEY, role),
                    listToString(permissions.getNotPermittedKeysPrefix(), ROLES_DELIM));
        });
        configMgmt.add().addFilepattern(ROLES_FILE).call();
    }

    private File getDirForClone(String folder)
    {
        File dir = new File(REPO_DIR, folder);
        int i = 0;
        while (dir.exists())
        {
            dir = new File(REPO_DIR, folder + i);
            i++;
        }
        return dir;
    }

    private Git cloneRepoForBranch(String branchName) throws GitAPIException
    {

        Git git = cloneRepository()
                .setURI(gitUrl)
                .setDirectory(getDirForClone(branchName))
                .setBranch(branchName)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, password))
                .call();
        return git;
    }

    @Override
    protected Map<String, Config> loadConfigsFromDataStore(String env)
    {
        try
        {
            branchRepoMap.get(env).pull().call();
            Map<String, Config> configs = new HashMap<>();
            traverseAndLoadProperties(configs, branchRepoMap.get(env).getRepository().getDirectory().getParentFile());
            return configs;
        } catch (GitAPIException | IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void traverseAndLoadProperties(Map<String, Config> configs, File root) throws IOException
    {
        for (File file : root.listFiles())
        {
            if (!file.getName().startsWith("."))
            {
                if (file.isDirectory())
                    traverseAndLoadProperties(configs, file);
                processFile(file, configs);
            }
        }

    }

    private void processFile(File file, Map<String, Config> configs) throws IOException
    {
        Properties props = new Properties();
        PropertiesUtil.load(props, file);
        props.forEach((k, v) ->
        {
            String key = (String) k;
            String appended = null;
            if (isStartsWithAny(appendFileNameFor, key))
            {
                String fileNamePrefix = getFilePathInRepo(file);
                fileNamePrefix += fileNameWithoutExt(file) + ".";
                key = fileNamePrefix + key;
                appended = fileNamePrefix;
            }
            Config config = ofNullable(configs.get(key)).orElseGet(Config::new);
            config.setValue(v);
            config.setAppended(appended);
            config.addLocation(file.getAbsolutePath());
            configs.put(key, config);
        });
    }

    private String getFilePathInRepo(File file)
    {
        try
        {
            String filePath = file.getParentFile().getCanonicalPath().substring(REPO_DIR.getCanonicalPath().length() + 1) + File.separator;
            return filePath.substring(filePath.indexOf(File.separator) + 1);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void saveConfigsToDataStore(String env, Map<String, Map<String, String>> locKeyValMap, String comment)
    {
        try
        {
            Git git = branchRepoMap.get(env);
            changeAndPush(git, () ->
            {
                locKeyValMap.forEach(PropertiesUtil::addOrUpdate);
                git.add().addFilepattern(".").call();
            }, comment);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
