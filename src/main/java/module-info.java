module DIM.Modifier.main {
    requires static lombok;
    requires org.slf4j;
    requires org.slf4j.simple;
    requires vb.dim.reader;
    requires javafx.controls;
    exports com.github.cfogrady.dim.modifier to javafx.graphics;
}