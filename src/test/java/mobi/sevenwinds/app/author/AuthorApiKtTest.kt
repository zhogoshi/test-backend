package mobi.sevenwinds.app.author

import io.restassured.RestAssured
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.jsonBody
import mobi.sevenwinds.common.toResponse
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthorApiKtTest : ServerTest() {

    @BeforeEach
    internal fun setUp() {
        transaction { AuthorTable.deleteAll() }
    }

    @Test
    fun testUserCreation() {
        addRecord(AuthorRecord("hogoshi"))
    }

    private fun addRecord(record: AuthorRecord) {
        RestAssured.given()
            .jsonBody(record)
            .post("/author/add")
            .toResponse<AuthorRecord>().let { response ->
                Assertions.assertEquals(record, response)
            }
    }

}