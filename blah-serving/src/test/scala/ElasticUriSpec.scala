package blah.serving

import org.scalatest._

class ElasticUriSpec extends FlatSpec with Matchers {

  "ElasticUri" should "from string" in {
    ElasticUri("elasticsearch://localhost:9200") should be (
      ElasticUri(List(("localhost", 9200))))
    ElasticUri("elasticsearch://foo:9200,bar:9300") should be (
      ElasticUri(List(("foo", 9200), ("bar", 9300))))
  }
}
