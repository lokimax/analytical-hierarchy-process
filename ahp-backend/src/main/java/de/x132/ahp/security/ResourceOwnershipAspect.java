package de.x132.ahp.security;

import de.x132.ahp.exception.ResourceNotFoundException;
import de.x132.ahp.exception.UnauthorizedException;
import java.util.Optional;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ResourceOwnershipAspect {

  private final ApplicationContext applicationContext;

  public ResourceOwnershipAspect(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Before("@annotation(checkOwnership)")
  public void checkOwnership(JoinPoint joinPoint, CheckOwnership checkOwnership) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] paramNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    String idParam = checkOwnership.idParam();
    Long id = null;

    for (int i = 0; i < paramNames.length; i++) {
      if (paramNames[i].equals(idParam)) {
        if (args[i] instanceof Long) {
          id = (Long) args[i];
          break;
        } else if (args[i] instanceof String) {
          try {
            id = Long.parseLong((String) args[i]);
            break;
          } catch (NumberFormatException e) {
            // Ignore, maybe not the ID we are looking for or invalid format
          }
        }
      }
    }

    if (id == null) {
      throw new IllegalArgumentException("Could not find ID parameter '" + idParam + "'");
    }

    CrudRepository repository = applicationContext.getBean(checkOwnership.repository());
    Optional<?> entityOpt = repository.findById(id);

    if (entityOpt.isEmpty()) {
      // We let the Controller handle 404 usually, or we throw it here.
      // If we throw here, we save the controller from checking it.
      // But typically ownership check implies existence check.
      throw new ResourceNotFoundException("Resource", "id", id);
    }

    Object entity = entityOpt.get();

    if (!(entity instanceof Ownable)) {
      throw new IllegalStateException(
          "Entity of type " + entity.getClass().getName() + " does not implement Ownable");
    }

    Ownable ownable = (Ownable) entity;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = authentication.getName();

    if (!ownable.getClient().getNickname().equals(currentUser)) {
      throw new UnauthorizedException("You do not have permission to access this resource");
    }
  }
}
