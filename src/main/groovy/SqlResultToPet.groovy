import org.apache.camel.examples.model.Pet;
import org.apache.camel.examples.model.Category;
import org.apache.camel.examples.model.Tag;

import java.util.Map;

def sql = request.body;

return new Pet(
  'id': sql[0]['ID'],
  'category': new Category(
                    'id': sql[0]['CATEGORY_ID'],
                    'name': sql[0]['CATEGORY_NAME']
                  ),
  'name': sql[0]['PET_NAME'],
  'photoUrls': ((sql['PHOTO_URL'] as Set) as List),
  'tags': sql.collect() { new Tag(
                                "id": it['TAG_ID'],
                                "name": it['TAG_NAME']
                              ) },
  'status': sql[0]['STATUS']?.toUpperCase()
);
