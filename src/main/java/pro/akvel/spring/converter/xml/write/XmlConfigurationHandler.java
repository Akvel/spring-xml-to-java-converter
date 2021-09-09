package pro.akvel.spring.converter.xml.write;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ELEMENT_BEAN;

@Log4j
public class XmlConfigurationHandler extends DefaultHandler {

    private final Set<BeanKey> convertedBeans;
    private final XMLStreamWriter xsw;

    private boolean write;
    private int counter;

    @SneakyThrows
    public XmlConfigurationHandler(Set<BeanKey> convertedBeans,
                                   String outputFilePath) throws IOException {
        this.convertedBeans = convertedBeans;
        XMLOutputFactory xof = XMLOutputFactory.newInstance();

        //Saxon used for pretty print new XML config only
        Configuration config = new Configuration();
        Processor p = new net.sf.saxon.s9api.Processor(config);
        Serializer s = p.newSerializer();
        s.setOutputProperty(Serializer.Property.METHOD, "xml");
        s.setOutputProperty(Serializer.Property.INDENT, "yes");
        s.setOutputStream(new FileOutputStream(outputFilePath));
        xsw = s.getXMLStreamWriter();
    }

    @SneakyThrows
    @Override
    public void startDocument() {
        super.startDocument();
        write = true;
        xsw.writeStartDocument();
    }

    @SneakyThrows
    @Override
    public void endDocument() {
        super.endDocument();
        xsw.writeEndDocument();
        xsw.flush();
        xsw.close();
    }


    @SneakyThrows
    @Override
    public void startElement(String uri, String localName,
                             String qName,
                             Attributes attributes) {
        super.startElement(uri, localName, qName, attributes);


        if (ELEMENT_BEAN.equals(qName)) {
            BeanKey beanKey = BeanKey.getInstance(attributes);
            if (beanKey != null && convertedBeans.contains(beanKey)) {
                log.debug("Skip bean " + XmlUtils.printAll(attributes));
                write = false;
            }

            if (!write) {
                counter++;
            }
        }

        if (write) {
            xsw.writeStartElement(uri, qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                xsw.writeAttribute(attributes.getQName(i),
                        attributes.getValue(i));
            }
        }
    }


    @SneakyThrows
    @Override
    public void characters(char[] ch, int start, int length) {
        super.characters(ch, start, length);

        if (write) {
            xsw.writeCharacters(ch, start, length);
        }
    }

    @SneakyThrows
    @Override
    public void endElement(String uri, String localName, String qName) {
        super.endElement(uri, localName, qName);

        if (write) {
            xsw.writeEndElement();
        }

        if (!write && ELEMENT_BEAN.equals(qName)) {
            counter--;
            if (counter == 0) {
                write = true;
                log.debug("Continue write");
            }
        }
    }
}
