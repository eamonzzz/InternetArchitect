package mydebug;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author eamonzzz
 * @date 2021-05-13 20:27
 */
public class Test {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("test${username}.xml");
		A a = context.getBean(A.class);
		System.out.println(a);

		A a1 = new A();
		ApplicationContext context1 = a1.getContext();

	}
}
