package proxy

trait DataSource {
  def executeQuery(query: String)
}
