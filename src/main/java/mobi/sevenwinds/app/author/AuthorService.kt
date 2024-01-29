package mobi.sevenwinds.app.author

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object AuthorService {

    suspend fun addRecord(author: AuthorRecord): AuthorRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = AuthorEntity.new {
                this.name = author.name
                this.createdAt = DateTime.now()
            }

            return@transaction entity.toResponse()
        }
    }

}