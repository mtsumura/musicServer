package services.storage

import org.slf4j.LoggerFactory

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.Region
import com.amazonaws.util.IOUtils

import services.document.DocumentServiceRetrieval

class DocumentService extends DocumentServiceRetrieval {
  final val log = LoggerFactory.getLogger(this.getClass());

  private val defaultRegion = Region.US_West_2
  private final val bucketName = "mtsumura-jukebox"
  private val s3 = getAwsClient
  private def getAwsClient: AmazonS3 = {
    log.info("connect to AWS: " + defaultRegion.toString())
    val builder = AmazonS3Client.builder()
    builder.setRegion(defaultRegion.toString())
    builder.build()
  }

  def getObject(key: String): Array[Byte] = {
    log.info("key:" + key)
    val getObjectRequest = new GetObjectRequest(bucketName, key)
    val s3ObjOpt =
      try {
        val s3Object = s3.getObject(getObjectRequest)
        Option(s3Object)
      } catch {
        case ex: SdkClientException => {
          log.error(ex.getMessage)
          None
        }
        case ex: AmazonServiceException => {
          log.error(ex.getMessage)
          None
        }
      }

    val obj = s3ObjOpt match {
      case Some(obj) => obj
      case None      => throw new Exception
    }

    val inputStream = obj.getObjectContent
    val byteArray = IOUtils.toByteArray(inputStream)
    byteArray
  }
}
