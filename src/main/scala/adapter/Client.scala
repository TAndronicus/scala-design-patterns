package adapter

object Client {
  def main(args: Array[String]): Unit = {
    val webRequester: WebRequester = new WebRequesterAdapter(new LegacyWebRequester)
    println(webRequester.request(42))
    println(StatelessWebRequesterAdapter.request(42))
  }
}
