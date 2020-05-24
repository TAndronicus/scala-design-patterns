package decorator

object Client {

  def main(args: Array[String]): Unit = {
    val webPage = new AuthenticatedWebPage(new AuthorizedWebPage(new SimpleWebPage))
    webPage.render()
  }

}
