module be.esi.prj.easyeval {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires tess4j;


    // This allows the FXMLLoader to access and instantiate controllers
    opens be.esi.prj.easyeval to javafx.fxml;
    opens be.esi.prj.easyeval.fxmlcontroller to javafx.fxml;

    // This allows JavaFX to work with your model properties
    opens be.esi.prj.easyeval.model to javafx.base;

    exports be.esi.prj.easyeval;
    exports be.esi.prj.easyeval.model;
    exports be.esi.prj.easyeval.repository;
    exports be.esi.prj.easyeval.viewmodel;
    exports be.esi.prj.easyeval.fxmlcontroller;
    exports be.esi.prj.easyeval.utils;
}