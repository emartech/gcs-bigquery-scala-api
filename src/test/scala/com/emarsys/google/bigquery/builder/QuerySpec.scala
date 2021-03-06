package com.emarsys.google.bigquery.builder

import com.emarsys.google.bigquery.model.BqTableReference
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class QuerySpec extends AnyWordSpec with Matchers {

  import QueryCondition._

  val customerId = 1234

  val tableReference = BqTableReference("project", "dataset", "table")

  "Standard TableQuery" should {

    "show query with no condition" in {
      val expectedQuery = "SELECT * FROM `project.dataset.table`"
      TableQuery(StandardTableSource(tableReference)).show shouldEqual expectedQuery
    }

    "use equals condition" in {
      val expectedQuery =
        s"SELECT * FROM `project.dataset.table` WHERE customer_id = $customerId"
      TableQuery(
        StandardTableSource(tableReference),
        "customer_id" === customerId
      ).show shouldEqual expectedQuery
    }

    "use given fields" in {
      val expectedQuery =
        s"SELECT count(*) FROM `project.dataset.table` WHERE customer_id = $customerId"
      TableQuery(
        StandardTableSource(tableReference),
        "customer_id" === customerId,
        "count(*)"
      ).show shouldEqual expectedQuery
    }

    "use having part" in {
      val expectedQuery =
        s"SELECT id FROM `project.dataset.table` WHERE customer_id = $customerId GROUP BY name HAVING id >= 100"
      val having =
        QueryHaving(groupByFields = List("name"), condition = "id" >>= 100)
      TableQuery(
        StandardTableSource(tableReference),
        "customer_id" === customerId,
        "id",
        having
      ).show shouldEqual expectedQuery
    }

    "use having part with additional fields needed by having" in {
      val expectedQuery =
        s"SELECT id, count(name) as id_count FROM `project.dataset.table` WHERE customer_id = $customerId GROUP BY name,desc HAVING id_count >= 1"
      val having = QueryHaving(
        List("count(name) as id_count"),
        List("name", "desc"),
        "id_count" >>= 1
      )
      TableQuery(
        StandardTableSource(tableReference),
        "customer_id" === customerId,
        "id",
        having
      ).show shouldEqual expectedQuery
    }

    "have legacy flag set to false" in {
      TableQuery(StandardTableSource(tableReference)).isLegacy shouldBe false
    }
  }

  "Legacy TableQuery" should {

    "show query with no condition" in {
      val expectedQuery = "SELECT * FROM [project:dataset.table]"
      TableQuery(LegacyTableSource(tableReference)).show shouldEqual expectedQuery
    }

    "use equals condition" in {
      val expectedQuery =
        s"SELECT * FROM [project:dataset.table] WHERE customer_id = $customerId"
      TableQuery(
        LegacyTableSource(tableReference),
        "customer_id" === customerId
      ).show shouldEqual expectedQuery
    }

    "use given fields" in {
      val expectedQuery =
        s"SELECT count(*) FROM [project:dataset.table] WHERE customer_id = $customerId"
      TableQuery(
        LegacyTableSource(tableReference),
        "customer_id" === customerId,
        "count(*)"
      ).show shouldEqual expectedQuery
    }

    "have legacy flag set to true" in {
      TableQuery(LegacyTableSource(tableReference)).isLegacy shouldBe true
    }

  }
}
