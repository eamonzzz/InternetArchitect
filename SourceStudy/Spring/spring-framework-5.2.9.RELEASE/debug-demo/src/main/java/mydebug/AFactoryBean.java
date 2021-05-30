package mydebug;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author eamonzzz
 * @date 2021-05-13 23:26
 */
public class AFactoryBean implements FactoryBean<A> {
	@Override
	public A getObject() throws Exception {
		return new A();
	}

	@Override
	public Class<?> getObjectType() {
		return A.class;
	}
}
