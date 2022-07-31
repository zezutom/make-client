import com.tomaszezula.make.client.js.MakeClient

@JsExport
fun create(token: String): MakeClient = MakeClient.create(token)
