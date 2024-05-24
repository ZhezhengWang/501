import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class XMLDemo {


    @Test
    public void saxParseXML() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        PersonHandler personHandler = new PersonHandler();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("person.xml");
        saxParser.parse(is, personHandler);
        List<Person> persons = personHandler.getPersons();
        for(Person p:persons){
            System.out.println(p);
        }
    }

}
