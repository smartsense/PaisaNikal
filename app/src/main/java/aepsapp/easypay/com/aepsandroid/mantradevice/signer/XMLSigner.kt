package aepsapp.easypay.com.aepsandroid.mantradevice.signer


import android.util.Log

import org.apache.xml.security.Init
import org.apache.xml.security.c14n.Canonicalizer
import org.apache.xml.security.signature.XMLSignature
import org.apache.xml.security.transforms.Transforms
import org.apache.xml.security.utils.Constants
import org.apache.xml.security.utils.ElementProxy
import org.w3c.dom.Document

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.Key
import java.security.KeyStore
import java.security.cert.X509Certificate

import javax.xml.parsers.DocumentBuilderFactory

/**
 *
 * Refer Site:  https://gist.github.com/rafaelwkerr/a585d324d3e534a2c16b
 * Gradle Or Jar for signer: https://mvnrepository.com/artifact/xml-security/xmlsec/1.3.0
 */
object XMLSigner {

    private val URI = "#NFe13140782373077000171650290000030531000030538"

    fun generateSignXML(inputXML: String, privateKeyStream: InputStream, keyPass: String): String {
        try {
            val stream = ByteArrayInputStream(inputXML.toByteArray())
            val f = DocumentBuilderFactory.newInstance()
            f.isNamespaceAware = true
            val doc = f.newDocumentBuilder().parse(stream)
            stream.close()
            Init.init()
            ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "")
            val xmlSignature = XMLSignature(doc, URI, XMLSignature.ALGO_ID_SIGNATURE_RSA)

            val transforms = Transforms(doc)
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE)

            xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1)

            val keyStore = KeyStore.getInstance("PKCS12")
            keyStore.load(privateKeyStream, keyPass.toCharArray())
            val alias = keyStore.aliases().nextElement()
            val dpkEntry = keyStore.getEntry(alias, KeyStore.PasswordProtection(keyPass.toCharArray())) as KeyStore.PrivateKeyEntry
            val privateKey = dpkEntry.privateKey
            val cert = keyStore.getCertificate(alias) as X509Certificate

            xmlSignature.addKeyInfo(cert)
            xmlSignature.addKeyInfo(cert.publicKey)

            xmlSignature.sign(privateKey)
            doc.documentElement.appendChild(xmlSignature.getElement())

            val outputStream = ByteArrayOutputStream()
            outputStream.write(Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS).canonicalizeSubtree(doc))
            return outputStream.toString()
        } catch (e: Exception) {
            Log.e("Error", "Error while generating Sign XML", e)
            return ""
        }

    }


}
