# config-mgmt
Management console to update configurations stored in Git repository.

This app will read all properties from given git repository, and on UI it will represent the configuration keys (which have words separated by dot) as tree structure. We can search for key or value to filter the tree. Admin can configure which role can edit which all configurations using 'settings' tab. This app will provide ability to modify configurations of each environment/branch separately. 

![alt text](https://github.com/rajeevnaikte/config-mgmt/blob/master/images/home.png)