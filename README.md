# hell-as-a-service

A little chaos monkey written in clojure

## Status

WIP

## Usage

### Components 

  * API: Permit to add, list, remove monkey jobs
  * ChaosMonkey: Reads monkey jobs in cassandra and execute some tasks.
<aside class="notice">
For now ChaosMonkey component is not scalable. If you scale this, you will have much more chaos … 
</aside>

### Databases
  * Use Cassandra to store monkey jobs

### Configuration file
There is a configuration file example in *config/template_config.edn*

For now the configuration file to use is store in environment variable called *IMMUCONF_CFG* 

There is a rc file *.rc* which is used to load environment variable in order to load the correct config file (By default this .rc merge some confs file)

Feel free to load your own config file:

```bash
export IMMUCONF_CFG=${PATH_CONFIG_FILE}
```

```csh
setenv IMMUCONF_CFG ${PATH_CONFIG_FILE}
```

if you want more informations about how configuration files are handled, please go here [immuconf project on github](https://github.com/levand/immuconf)

### Run the API locally

`lein ring server`

### Run the ChaosMonkey

`lein run`

### Run the tests
`lein test`

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
