
package apartamentos.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 *
 * @author Jonathan
 */

@Controller
@Slf4j
@RequestMapping("/menu")
public class registrocontroller {
    
    @GetMapping("/registro")
    public String inicio() {
        return "menu/registro";
    }
}
