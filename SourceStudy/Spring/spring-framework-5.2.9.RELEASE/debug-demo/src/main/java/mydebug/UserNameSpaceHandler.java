package mydebug;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author eamonzzz
 * @date 2021-06-17 23:20
 */
public class UserNameSpaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("user", new UserBeanDefinitionParser());
	}
}
