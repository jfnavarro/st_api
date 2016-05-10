package com.st.controller;


//package com.spatialtranscriptomics.controller;
//
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import com.spatialtranscriptomics.exceptions.BadRequestResponse;
//import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
//import com.spatialtranscriptomics.model.Credential;
//import com.spatialtranscriptomics.serviceImpl.CredentialServiceImpl;
//

/*
 * This class is Spring MVC controller class for the API endpoint "rest/credentials".
 * It implements the methods available at this endpoint.
 * This endpoint is not used currently. It's been abandoned because not required at the momen.
 *  The purpose would be to reset/update passwords.
 */

//@Repository
//@Controller
//@RequestMapping("/rest/credentials")
//public class CredentialController {
//
//	private static final Logger logger = Logger
//			.getLogger(CredentialController.class);
//
//	@Autowired
//	PasswordEncoder passwordEncoder;
//
//	@Autowired
//	CredentialServiceImpl credentialService;
//
//	// @RequestMapping(method = RequestMethod.GET)
//	// public @ResponseBody void setPwd(@RequestParam(value="pwd",
//	// required=true) String pwd) {
//	//
//	// //return passwordEncoder.encode(pwd);
//	// }
//
//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody
//	void updateCredentials(
//			@RequestParam(value = "username", required = true) String username,
//			@RequestParam(value = "currentPwd", required = false) String currentPwd,
//			@RequestParam(value = "newPwd", required = true) String newPwd) {
//
//		Credential cred = credentialService.findByUsername(username);
//
//		if (cred == null) {
//			throw new CustomBadRequestException("Username does not exist.");
//		}
//
//		String dbPwd = cred.getPassword();
//
//		if (currentPwd == null && dbPwd != null) {
//			throw new CustomBadRequestException("Missing current password.");
//		}
//
//		if (dbPwd == null || passwordEncoder.matches(currentPwd, dbPwd)) {
//			cred.setPassword(passwordEncoder.encode(newPwd));
//			credentialService.update(cred);
//		} else {
//			throw new CustomBadRequestException(
//					"The old password or username is wrong.");
//		}
//		logger.debug("ok");
//
//	}
//
//	@ExceptionHandler(CustomBadRequestException.class)
//	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
//	public @ResponseBody
//	BadRequestResponse handleNotFoundException(CustomBadRequestException ex) {
//		return new BadRequestResponse(ex.getMessage());
//	}
//
//}
