package mydebug;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author eamonzzz
 * @date 2021-05-13 20:38
 */
public class A implements ApplicationContextAware, BeanNameAware {
	private ApplicationContext context;

	private String name;

	public ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public void setBeanName(String name) {
		this.name = name;
		System.out.println("name: "+name);
	}
}
