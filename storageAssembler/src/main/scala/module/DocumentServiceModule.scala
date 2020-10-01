package module

import com.google.inject.AbstractModule
import services.document.DocumentServiceRetrieval
import services.storage.DocumentService

class DocumentServiceModule extends AbstractModule {
  def configure() = {
    bind(classOf[DocumentServiceRetrieval])
    .to(classOf[DocumentService])
  }
}