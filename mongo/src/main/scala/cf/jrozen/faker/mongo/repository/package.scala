package cf.jrozen.faker.mongo

import com.mongodb.client.model.Filters
import org.bson.conversions.Bson

package object repository {

  val workspaceNameFilter: String => Bson = (name: String) => Filters.eq("name", name)

}
