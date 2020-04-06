# jenkins-configuration

This repo is intended to keep the setup configuration of our Jenkins installations. A Jenkinsfile is included to easily build new Jenkins master images.

Our current pre-configured Jenkins master docker image makes use of the scripts found in `groovy-scripts` and `casc_configs` directories. For the Jenkins theme, we can add (or remove) themes from the `user-content` directory. For plugins installations or set a specific version for a plugin, we can update the file `plugins/plugins.txt`.

The environment variables needed for the configuration scripts found in this repo are defined either in Terraform or in Vault.
