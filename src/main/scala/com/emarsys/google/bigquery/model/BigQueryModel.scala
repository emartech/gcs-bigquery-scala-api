package com.emarsys.google.bigquery.model

import com.emarsys.google.bigquery.builder._
import com.google.api.services.bigquery.model._

import scala.collection.JavaConverters._
import com.google.cloud.bigquery.JobInfo.{CreateDisposition, WriteDisposition}

sealed trait BigQueryModel

case class BqTableReference(project: String, dataSet: String, table: String) extends BigQueryModel {
  def toJava =
    new TableReference()
      .setProjectId(project)
      .setDatasetId(dataSet)
      .setTableId(table)

  val legacyName   = s"[$project:$dataSet.$table]"
  val standardName = s"`$project.$dataSet.$table`"
}

case class BqTableFieldSchema(fieldName: String, fieldType: String) extends BigQueryModel {
  def toJava =
    new TableFieldSchema().setName(fieldName).setType(fieldType)
}

case class BqTableSchema(fields: List[BqTableFieldSchema]) extends BigQueryModel {
  def toJava =
    new TableSchema().setFields(fields.map(_.toJava).asJava)
}

case class BqTableRow(elem: (String, Any)*) extends BigQueryModel {
  def toJava = {
    val data = new TableRow()
    elem.foreach(r => data.set(r._1, r._2))
    data
  }
}

case class BqTableData(rows: BqTableRow*) extends BigQueryModel {
  def toJava = {
    new TableDataInsertAllRequest()
      .setRows(
        rows
          .map(row => new TableDataInsertAllRequest.Rows().setJson(row.toJava))
          .asJava
      )
  }
}

case class BqQueryJobConfig(
    query: Query,
    destinationTable: Option[BqTableReference],
    disposition: Option[WriteDisposition]
) extends BigQueryModel {

  def toJava = {
    val config = new JobConfigurationQuery()
      .setQuery(query.show)
      .setUseLegacySql(query.isLegacy)
    val configWithDestination =
      destinationTable.fold(config)(dt => config.setDestinationTable(dt.toJava))
    val configAll = disposition.fold(configWithDestination)(
      d => config.setWriteDisposition(d.toString)
    )

    configAll
  }
}

case class BqQueryJob(jobConfig: BqQueryJobConfig, labels: Map[String, String]) extends BigQueryModel {
  import collection.JavaConverters._
  def toJava: Job =
    new Job()
      .setConfiguration(new JobConfiguration().setQuery(jobConfig.toJava).setLabels(labels.asJava))
}

case class BqLoadJobConfig(
    destinationTable: BqTableReference,
    schema: BqTableSchema,
    sourceFormat: FileFormat = CsvFormat,
    disposition: CreateDisposition = CreateDisposition.CREATE_IF_NEEDED
) extends BigQueryModel {

  def toJava: JobConfigurationLoad = {
    new JobConfigurationLoad()
      .setDestinationTable(destinationTable.toJava)
      .setSchema(schema.toJava)
      .setSourceFormat(sourceFormat.show)
      .setCreateDisposition(disposition.toString)
  }
}

case class BqLoadJob(jobConfig: BqLoadJobConfig) extends BigQueryModel {
  def toJava: Job =
    new Job()
      .setConfiguration(new JobConfiguration().setLoad(jobConfig.toJava))
      .setJobReference(
        new JobReference().setProjectId(jobConfig.destinationTable.project)
      )
}

case class BqExtractJobConfig(
    sourceTable: BqTableReference,
    destinationUri: String,
    printHeader: Boolean = false,
    fieldDelimiter: String = ",",
    destinationFormat: FileFormat = CsvFormat,
    compression: String = "NONE"
) extends BigQueryModel {

  def toJava: JobConfigurationExtract = {
    new JobConfigurationExtract()
      .setSourceTable(sourceTable.toJava)
      .setDestinationUri(destinationUri)
      .setPrintHeader(printHeader)
      .setFieldDelimiter(fieldDelimiter)
      .setDestinationFormat(destinationFormat.show)
      .setCompression(compression)
  }

}

case class BqExtractJob(jobConfig: BqExtractJobConfig, labels: Map[String, String]) extends BigQueryModel {
  import collection.JavaConverters._
  def toJava: Job = {
    new Job()
      .setConfiguration(new JobConfiguration().setExtract(jobConfig.toJava).setLabels(labels.asJava))
  }
}
