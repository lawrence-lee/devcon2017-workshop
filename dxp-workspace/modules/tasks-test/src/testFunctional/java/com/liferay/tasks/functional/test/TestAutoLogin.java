package com.liferay.tasks.functional.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PortalUtil;

@Component(immediate = true, service = AutoLogin.class)
public class TestAutoLogin extends BaseAutoLogin {
	
	@Override
	protected String[] doLogin(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		String[] credentials = null;

		long companyId = PortalUtil.getCompanyId(request);

		User autoLoginUser = null;

		try {
			autoLoginUser = _userLocalService.getUserByEmailAddress(
				companyId, "test@liferay.com");
		}
		catch (Exception e) {
		}

		if (autoLoginUser != null) {
			credentials = new String[3];

			credentials[0] = Long.toString(autoLoginUser.getUserId());
			credentials[1] = autoLoginUser.getPassword();
			credentials[2] = Boolean.toString(true);
		}

		return credentials;
	}

	@Reference
	private UserLocalService _userLocalService;
}
