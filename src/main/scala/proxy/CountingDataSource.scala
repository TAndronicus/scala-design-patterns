package proxy

object CountingDataSource extends DataSource {
  private var counter = 0
  override def executeQuery(query: String): Unit = {
    counter += 1
    SimpleDataSource.executeQuery(query)
  }
  def getNumberOfDbQueries = counter
}
