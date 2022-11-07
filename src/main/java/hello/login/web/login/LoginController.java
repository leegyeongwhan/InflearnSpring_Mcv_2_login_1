package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //  @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult
            , HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("loginMember={}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지않습니다");
            return "login/loginForm";
        }

        // 로그인 성공처리 TODO

        //쿠키에 시간정보를 주지않으면 세션쿠키가된다.(브라우저 종료시 종료
        Cookie idCookie = new Cookie("memberID", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    // @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult
            , HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("loginMember={}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지않습니다");
            return "login/loginForm";
        }

        // 로그인 성공처리 TODO

        //세션관리자를 통해세션을 생성하고 회원데이터를 보관
        //쿠키에 시간정보를 주지않으면 세션쿠키가된다.(브라우저 종료시 종료


        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult
            , HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("loginMember={}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지않습니다");
            return "login/loginForm";
        }

        // 로그인 성공처리 TODO

        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        //세션관리자를 통해세션을 생성하고 회원데이터를 보관
        //쿠키에 시간정보를 주지않으면 세션쿠키가된다.(브라우저 종료시 종료


        return "redirect:/";
    }

   // @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
