package proxy

object SimpleDataSource extends DataSource {
  override def executeQuery(query: String): Unit = println(query)
}
