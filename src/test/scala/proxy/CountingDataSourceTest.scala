package proxy

import org.scalatest.funsuite.AnyFunSuite

class CountingDataSourceTest extends AnyFunSuite {

  test("Should count number of db shots") {
    CountingDataSource.executeQuery("select * from people;")
    CountingDataSource.executeQuery("insert into people(id, name) values (1, 'John Doe')")
    assert(CountingDataSource.getNumberOfDbQueries == 2)
  }

}
