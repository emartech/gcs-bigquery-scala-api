# BigQuery Scala Api

[![Codeship Status for emartech/gcs-bigquery-scala-api](https://app.codeship.com/projects/68e10a10-121a-0135-5667-2a4a553df23d/status?branch=master)](https://app.codeship.com/projects/216915)
[![Maven Central](https://img.shields.io/maven-central/v/com.emarsys/gcs-bigquery-scala-api_2.12.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.emarsys/gcs-bigquery-scala-api_2.12)
[![Maven Central](https://img.shields.io/maven-central/v/com.emarsys/gcs-bigquery-scala-api_2.13.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.emarsys/gcs-bigquery-scala-api_2.13)


## Overview
There are many libraries for Google Cloud Platform except for Scala.

Currently we aim at 
 * Create BigQuery jobs for running queries and tasks
 
## Getting Started
 Create api instance:
        
        val bigQueryInstance = BigQueryApi(projectName, Config.credentialWrite)

 Create TableReference for a BigQuery table:
 
        val tableReference = BqTableReference("[project]", "[dataSet]", "[table]")
 
 Create command factory which will use the api instance to create commands:
 
         val commandFactory = new CommandFactory {
              override implicit val bigQuery = bigQueryInstance
         }

 Following table commands can be used:
 * drop table
 * create table
 * insert data
 
 Following job commands can be used:
  * copy data to table (TableReference)
  * extract data to url in file format (default csv) 
  * insert data from file (default csv)

## Testing

Specify the `GCS_PROJECT_NAME`, `GCS_PRIVATE_KEY_ID`, `GCS_READ_KEY`, `GCS_EMAIL_PREFIX` and `GCS_CLIENT_ID` env vars in the `.env` file, then run `sbt test` or `sbt it:test`.

## Creating a release

This library is using [sbt-release-early] for releasing artifacts. Every push will be released to maven central, see the plugins documentation on the versioning schema.

### To cut a final release:

Choose the appropriate version number according to [semver] then create and push a tag with it, prefixed with `v`.
For example:

```
$ git tag -s v1.1.1
$ git push --tag
```

After pushing the tag, while it is not strictly necessary, please [draft a release on github] with this tag too.

[sbt-release-early]: https://github.com/scalacenter/sbt-release-early
[semver]: https://semver.org
[draft a release on github]: https://github.com/emartech/gcs-bigquery-scala-api/releases/new
