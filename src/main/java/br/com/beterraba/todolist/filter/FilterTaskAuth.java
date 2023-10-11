package br.com.beterraba.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.beterraba.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorization = request.getHeader("Authorization");

        var authEncoded = authorization.substring("Basic".length()).trim();
        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
        var authString = new String(authDecoded);

        String[] credentials = authString.split(":");

        var user = this.userRepository.findByUsername(credentials[0]);
        if (user == null) {
            response.sendError(401);
        } else {
            var isValidPassword = BCrypt.verifyer().verify(credentials[1].toCharArray(),user.getPassword());
            if (isValidPassword.verified) {
                filterChain.doFilter(request,response);
            } else {
                response.sendError(401);
            }

        }

    }
}
