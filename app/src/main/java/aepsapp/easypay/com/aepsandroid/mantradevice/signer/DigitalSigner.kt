package aepsapp.easypay.com.aepsandroid.mantradevice.signer

import android.content.Context
import android.util.Log

import org.apache.xml.security.Init
import org.apache.xml.security.algorithms.MessageDigestAlgorithm
import org.apache.xml.security.exceptions.XMLSecurityException
import org.apache.xml.security.keys.content.X509Data
import org.apache.xml.security.signature.XMLSignature
import org.apache.xml.security.transforms.Transforms
import org.apache.xml.security.utils.ElementProxy
import org.w3c.dom.Document
import org.xml.sax.InputSource

import java.io.StringReader
import java.io.StringWriter
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Created by SW11 on 3/18/2017.
 */

class DigitalSigner(keyStoreFile: String, keyStorePassword: CharArray, context: Context) {
    private var alias: String? = null
    private var ks: KeyStore? = null
    private var privateKey: PrivateKey? = null

    init {
        try {
            this.ks = KeyStore.getInstance(KEY_STORE_TYPE)
            this.ks!!.load(context.assets.open(keyStoreFile), keyStorePassword)
            this.alias = this.ks!!.aliases().nextElement()
            this.privateKey = this.ks!!.getKey(alias, keyStorePassword) as PrivateKey
        } catch (e: Exception) {
            Log.e("eror", "erro")
            e.printStackTrace()
        }

    }

    fun signXML(xmlDocument: String): String {
        try {
            val dbf = DocumentBuilderFactory.newInstance()
            dbf.isNamespaceAware = true
            val signedDocument = sign(dbf.newDocumentBuilder().parse(InputSource(StringReader(xmlDocument))))
            val stringWriter = StringWriter()
            TransformerFactory.newInstance().newTransformer().transform(DOMSource(signedDocument), StreamResult(stringWriter))
            return stringWriter.buffer.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error while digitally signing the XML document", e)
        }

    }

    @Throws(Exception::class)
    private fun sign(xmlDoc: Document): Document {
        val x509Cert = this.ks!!.getCertificate(this.alias) as X509Certificate
        val signature = XMLSignature(xmlDoc, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1)
        //        XMLSignature signature = new XMLSignature(xmlDoc, StringUtils.EMPTY, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
        xmlDoc.documentElement.appendChild(signature.getElement())
        val transforms = Transforms(xmlDoc)
        transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE)
        //        signature.addDocument(StringUtils.EMPTY, transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1);
        signature.addDocument("", transforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1)
        val x509Data = X509Data(xmlDoc)
        signature.getKeyInfo().add(x509Data)
        x509Data.addSubjectName(x509Cert.subjectX500Principal.name)
        x509Data.addCertificate(x509Cert)
        signature.sign(this.privateKey)
        return xmlDoc
    }

    companion object {

        private val KEY_STORE_TYPE = "PKCS12"


        init {

            Init.init()
            try {
                ElementProxy.setDefaultPrefix("http://www.w3.org/2000/09/xmldsig#", "")
            } catch (e: XMLSecurityException) {
                e.printStackTrace()
            }

        }
    }
}
