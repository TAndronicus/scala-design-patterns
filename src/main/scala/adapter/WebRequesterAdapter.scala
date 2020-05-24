package adapter

class WebRequesterAdapter(legacyWebRequester: LegacyWebRequester) extends WebRequester {
  override def request(obj: Any): String = {
    val request = toJson(obj)
    legacyWebRequester.sendRequest(request)
  }
  def toJson(obj: Any) = obj.toString
}
