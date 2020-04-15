package net.eduard.api.lib.storage;

import net.eduard.api.lib.modules.Extra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Sistema de armazenamento automatizado baseado na reflexão das classes
 * 
 * 
 * 
 * @author Eduard
 * @see Extra
 * @version 3.0
 */
public interface Storable {


	@Target({ java.lang.annotation.ElementType.FIELD, ElementType.TYPE, })
	@Retention(RetentionPolicy.RUNTIME)
	 @interface StorageAttributes {

		boolean reference() default false;

		boolean indentificate() default false;

		boolean inline() default false;

		//boolean auto() default true;

	}

	/**
	 * Cria um Objeto pelo Mapa
	 * 
	 * @param map Mapa
	 * @return Objeto
	 */
	 default Object restore(Map<String, Object> map) {

		return null;
	}

	/**
	 * Salva o Objeto no Mapa
	 * 
	 * @param map    Mapa
	 * @param object Objeto
	 */
	 default void store(Map<String, Object> map, Object object) {

	}

	/**
	 * Gera uma nova instancia do objeto
	 * 
	 *
	 * @return Nova Instancia
	 */
	 default Object newInstance() {
		try {
			return Extra.getNew(getClass());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 *  Cria um novo Objeto pelo Objeto String
	 * @param object Objeto String
	 * @return Objeto
	 */
	 default Object restore(Object object) {

		return null;
	}
	/**
	 * Gera um objeto armazenal em STRING apartir do Objeto 1
	 * @param object Objeto
	 * @return String Objeto
	 */
	 default Object store(Object object) {
		return null;
	}

}
