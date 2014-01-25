package module.sensor.sensor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.db.ebean.Model;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface SensorPropertyDbDescription {

    Class<? extends Model> modelClass ();
    String modelClassColumnField();
    String tableName();
    String columnName();
    
}
