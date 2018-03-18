package com.rajeevn.common.util;

import com.rajeevn.common.interfaces.ThrowableConsumer;

import java.io.File;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.rajeevn.common.util.CollectionsUtil.flatten;
import static com.rajeevn.common.util.FileIOUtil.delete;
import static com.rajeevn.common.util.FileIOUtil.deleteQuietly;
import static com.rajeevn.common.util.FileIOUtil.move;
import static com.rajeevn.common.util.FileIOUtil.moveQuietly;
import static com.rajeevn.common.util.FileIOUtil.processOutputFile;
import static java.util.stream.Collectors.joining;

/**
 * It is a command utility for creating file with data. It provides many options to add event handlers
 * while creating file.
 *
 * @author Rajeev Naik
 * @since 2018/03/04
 * @param <S> class which extends {@link State}
 */
public class FileCreator<S extends FileCreator.State>
{
    private S state;
    private byte[] data;
    private boolean createEmptyIfNoData = false;
    private ThrowableConsumer<S, ?> onFileExists;
    private Consumer<S> onWriteComplete;
    private BiConsumer<S, Exception> onWriteFail;
    private Function<S, byte[]> dataProvider;

    /**
     * Object which holds state of execution. This will be provided to event handlers as parameter.
     * Custom state object can be created by extending this.
     */
    public static class State
    {
        private File file;
        private boolean fileExists = false;
        private boolean writeComplete = false;
        private boolean writing = false;

        protected State(File file)
        {
            this.file = file;
        }

        protected State(String filePath)
        {
            this.file = new File(filePath);
        }

        public File getFile()
        {
            return file;
        }

        public boolean isFileExists()
        {
            return fileExists;
        }

        public boolean isWriteComplete()
        {
            return writeComplete;
        }

        public boolean isWriting()
        {
            return writing;
        }

        void setFileExists(boolean fileExists)
        {
            this.fileExists = fileExists;
        }

        void setWriteComplete(boolean writeComplete)
        {
            this.writeComplete = writeComplete;
        }

        void setWriting(boolean writing)
        {
            this.writing = writing;
        }

        /**
         * Call this method to stop the calling function provided in {@link #setDataProvider(Function)}
         */
        public void finishWriting()
        {
            this.writing = false;
        }
    }

    public FileCreator(S state)
    {
        this.state = state;
    }

    /**
     * Set data to be written into file
     * @param data
     * @return
     */
    public FileCreator<S> setData(byte[] data)
    {
        this.data = data;
        return this;
    }

    /**
     * This will completely flatten the collection provided and call {@link Object#toString()} on each item
     * and these strings will be written into file as lines.
     *
     * @param data
     * @param <E>
     */
    public <E> FileCreator<S> setData(Collection<E> data)
    {
        if (data != null)
        {
            this.data = (flatten(data)
                    .map(o -> o.toString()))
                    .collect(joining(System.lineSeparator()))
                    .getBytes();
        }
        return this;
    }

    /**
     * This function will be called indefinitely till {@link State#finishWriting()} is called.
     *
     * @param provider
     */
    public FileCreator<S> setDataProvider(Function<S, byte[]> provider)
    {
        this.dataProvider = provider;
        return this;
    }

    /**
     * Indicate whether need to create empty file if no data.
     * @return
     */
    public FileCreator<S> createEmptyIfNoData()
    {
        this.createEmptyIfNoData = true;
        return this;
    }

    /**
     * Event handler which will be called when the given file is already exists.
     *
     * @param action
     * @return
     */
    public FileCreator<S> onFileExists(ThrowableConsumer<S, ?> action)
    {
        this.onFileExists = action;
        return this;
    }

    /**
     * Indicate to move the existing file to a location.
     *
     * @param moveToFilePath
     * @return
     */
    public FileCreator<S> onFileExistsMove(String moveToFilePath)
    {
        this.onFileExists = (state) -> move(state.getFile(), moveToFilePath);
        return this;
    }

    /**
     * Indicate to move the existing file to a location without throwing exception if move fails.
     *
     * @param moveToFilePath
     * @return
     */
    public FileCreator<S> onFileExistsMoveQuietly(String moveToFilePath)
    {
        this.onFileExists = (state) -> moveQuietly(state.getFile(), moveToFilePath);
        return this;
    }

    /**
     * Indicate to delete the existing file to a location.
     * @return
     */
    public FileCreator<S> onFileExistsDelete()
    {
        this.onFileExists = (state) -> delete(state.getFile());
        return this;
    }

    /**
     * Indicate to delete the existing file to a location without throwing exception if delete fails.
     * @return
     */
    public FileCreator<S> onFileExistsDeleteQuietly()
    {
        this.onFileExists = (state) -> deleteQuietly(state.getFile());
        return this;
    }

    /**
     * Hanlder which will be called when writing to file is complete.
     *
     * @param action
     * @return
     */
    public FileCreator<S> onWriteComplete(Consumer<S> action)
    {
        this.onWriteComplete = action;
        return this;
    }

    /**
     * Handler which will be called when writing to file is failed
     *
     * @param action
     * @return
     */
    public FileCreator<S> onWriteFail(BiConsumer<S, Exception> action)
    {
        this.onWriteFail = action;
        return this;
    }

    /**
     * Command to execute file creation using provided handlers.
     * @return
     */
    public S create() throws Exception
    {
        File file = this.state.getFile();
        if (file.exists())
        {
            this.state.setFileExists(true);
            if (this.onFileExists != null)
                this.onFileExists.accept(this.state);
        }

        if (this.data != null || this.dataProvider != null || this.createEmptyIfNoData)
        {
            try
            {
                processOutputFile(file, out ->
                {
                    this.state.setWriting(true);
                    if (this.data != null)
                        out.write(this.data);
                    else if (this.dataProvider != null)
                    {
                        while (this.state.isWriting())
                        {
                            byte[] data = this.dataProvider.apply(this.state);
                            if (data != null)
                                out.write(data);
                        }
                    }
                });
                this.state.setWriteComplete(true);
                if (this.onWriteComplete != null)
                {
                    try
                    {
                        this.onWriteComplete.accept(this.state);
                    } catch (Exception e)
                    {
                    }
                }
            } catch (Exception e)
            {
                this.state.setWriteComplete(false);
                if (this.onWriteFail != null)
                    this.onWriteFail.accept(this.state, e);
            }
        }

        return this.state;
    }
}
