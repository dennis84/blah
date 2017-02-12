package blah.serving

import org.scalatest._

class ElasticUriSpec extends FlatSpec with Matchers {

  "A ElasticUri" should "create from string" in {
    ElasticUri("localhost:9200") should be (
      ElasticUri(List(("localhost", 9200))))
    ElasticUri("foo:9200,bar:9300") should be (
      ElasticUri(List(("foo", 9200), ("bar", 9300))))
  }
}
