package adapter

object StatelessWebRequesterAdapter extends WebRequester {
  val legacyWebRequester = new LegacyWebRequester
  override def request(obj: Any): String = {
    val request = toJson(obj)
    legacyWebRequester.sendRequest(request)
  }
  def toJson(obj: Any) = obj.toString
}
