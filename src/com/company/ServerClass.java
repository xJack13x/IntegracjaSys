package com.company;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Endpoint;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;



@WebService
@SOAPBinding(style=SOAPBinding.Style.RPC)
public class ServerClass {
    public int countLaptops(String nazwaProducenta) {
        Database db = new Database();
        db.createConnection();
        try {
            int liczba = db.countRecords(nazwaProducenta);
            return liczba;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }


    }
    public int countRes(String rozdzcEkranuBox) {
        Database db = new Database();
        db.createConnection();
        try {
            int liczba = db.countRecordsRes(rozdzcEkranuBox);
            db.closeConnection();
            return liczba;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void generateXmlService(HashMap list) {
        Database db = new Database();
        db.createConnection();
        String query = "CALL `openLaptopy`();";
        ResultSet rs = db.prepareStatement(query);

            try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("Laptop");
                doc.appendChild(rootElement);
                for(int i=0; i<5; i++){
                    Element manufacturer = doc.createElement("manufacturer");
                    try {
                        manufacturer.setTextContent(rs.getString("manufacturer"));
                        rootElement.appendChild(manufacturer);
                        Element resolution = doc.createElement("resolution");
                        resolution.setTextContent(rs.getString("resolution"));
                        manufacturer.appendChild(resolution);
                        Element screenSize = doc.createElement("screenSize");
                        screenSize.setTextContent(rs.getString("screenSize"));
                        manufacturer.appendChild(screenSize);
                        Element screenCoating = doc.createElement("screenCoating");
                        screenCoating.setTextContent(rs.getString("screenCoating"));
                        manufacturer.appendChild(screenCoating);
                        Element cpu = doc.createElement("cpu");
                        cpu.setTextContent(rs.getString("cpu"));
                        manufacturer.appendChild(cpu);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                Transformer t = null;
                try {
                    t = TransformerFactory.newInstance().newTransformer();
                    try {
                        t.transform(new DOMSource(doc), new StreamResult(new File("C:\\is\\doc.xml")));
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }


    public static void main(String[] args) {
        String url = "http://localhost:1234/ServerClass";
        Endpoint.publish(url, new ServerClass());
        System.out.println("Service started @ " + url);
    }

}
