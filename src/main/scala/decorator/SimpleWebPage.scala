package decorator

class SimpleWebPage extends WebPage {
  override def render(): Unit = println("Rendered page")
}
