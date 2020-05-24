package adapter

trait WebRequester {
  def request(obj: Any): String
}
