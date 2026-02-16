package io.sunbit.app.util;

import org.springframework.beans.factory.annotation.Autowired;

import io.sunbit.app.entity.Expense;
import io.sunbit.app.security.jwt.JwtAuthenticationUtil;

public class ExpenseUserIdCheckUtil {

	@Autowired
	private static JwtAuthenticationUtil jwtAuthUtil;

	public static Boolean compareExpenseUserId(String token, Expense expense, Integer requestUserId) throws Exception {
		boolean isSameUser = false;
		// 1º - We check if the EXPENSE exists in DB.
		if (ExpenseUtil.existsExpenseInDb(expense) == null) {
			// 2º - We extract the ID USER from token.
			String subject = jwtAuthUtil.getSubject(token);
			String[] arrSubject = subject.split(",");
			int tokenUserId = Integer.parseInt(arrSubject[0]);
			// Test.
			System.out.println("TOKEN User Id: " + tokenUserId);
			// 3º - We compare the ID USER from token with the HTTP param 'requestUserId'
			if (tokenUserId == requestUserId)
				isSameUser = true;
		} else {
			throw new Exception("The EXPENSE you're trying to save IS ALREADY in DB");
		}
		return isSameUser;
	}
}
