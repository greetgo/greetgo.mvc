package kz.greetgo.mvc.security;

import static kz.greetgo.mvc.util.Base64Util.base64ToBytes;
import static kz.greetgo.mvc.util.Base64Util.bytesToBase64;

import java.util.Arrays;

import kz.greetgo.mvc.interfaces.MvcTrace;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelHandler;
import kz.greetgo.util.events.EventHandler;
import kz.greetgo.util.events.HandlerKiller;

public final class SecurityTunnelWrapper implements TunnelHandler {
  
  private final TunnelHandler whatWrapping;
  private final SecurityProvider provider;
  private final SessionStorage sessionStorage;
  private final SecurityCrypto sessionCrypto;
  private final SecurityCrypto signatureCrypto;
  
  public static MvcTrace trace;
  
  public SecurityTunnelWrapper(TunnelHandler whatWrapping, SecurityProvider provider,
      SessionStorage sessionStorage, SecurityCrypto sessionCrypto, SecurityCrypto signatureCrypto) {
    this.whatWrapping = whatWrapping;
    this.provider = provider;
    this.sessionStorage = sessionStorage;
    this.sessionCrypto = sessionCrypto;
    this.signatureCrypto = signatureCrypto;
  }
  
  @Override
  public void handleTunnel(final RequestTunnel tunnel) {
    if (trace != null) trace.trace("CP HDBEwehrewh START!!!");
    final String target = tunnel.getTarget();
    
    if (trace != null) trace.traceInTunnel("start target = " + target, tunnel);
    
    if (provider.skipSession(target)) {
      if (trace != null) trace.trace("CP djsanjer3 skipSession");
      whatWrapping.handleTunnel(tunnel);
      return;
    }
    
    final boolean underSecurityUmbrella = provider.isUnderSecurityUmbrella(target);
    
    if (trace != null) trace.trace("underSecurityUmbrella = " + underSecurityUmbrella);
    
    final byte[] bytesInStorage;
    
    {
      final String sessionBase64 = tunnel.cookies().getFromRequest(provider.cookieKeySession());
      byte[] bytes = base64ToBytes(sessionBase64);
      
      if (trace != null) trace.trace("CP HEBWJD bytes "
          + (bytes == null ? "is null" :" is not null"));
      
      if (sessionCrypto != null) {
        if (trace != null) trace.trace("CP GRHWVe sessionCrypto != null");
        bytes = sessionCrypto.decrypt(bytes);
      }
      
      if (underSecurityUmbrella && bytes != null && signatureCrypto != null) {
        if (trace != null) trace.trace("CP QTTSFrt");
        String signatureBase64 = tunnel.cookies().getFromRequest(provider.cookieKeySignature());
        if (trace != null) trace.trace("CP h2hrhbrfer signatureBase64 == null -> "
            + (signatureBase64 == null));
        byte[] signature = base64ToBytes(signatureBase64);
        if (!signatureCrypto.verifySignature(bytes, signature)) {
          if (trace != null) trace.trace("CP QKMRTBG bytes := null");
          bytes = null;
        }
      }
      
      if (trace != null) trace.trace("CP hrebtrhet");
      sessionStorage.setSessionBytes(bytesInStorage = bytes);
    }
    
    final EventHandler writeSessionToCookies = new EventHandler() {
      boolean performed = false;
      
      @Override
      public void handle() {
        if (performed) return;
        performed = true;
        
        if (trace != null) trace.trace("CP h6h34vyt");
        
        byte[] bytes = sessionStorage.getSessionBytes();
        if (trace != null) trace.trace("CP 543nj654 "
            + (bytes == null ? "bytes is null" :"bytes is not null"));
        
        if (Arrays.equals(bytesInStorage, bytes)) {
          if (trace != null) trace.trace("CP krmet54 EQUALS");
          return;
        }
        
        if (trace != null) trace.trace("CP ktmreyr");
        
        if (bytes == null) {
          if (trace != null) trace.trace("CP uyu76gfh4 bytes == null");
          tunnel.cookies().removeFromResponse(provider.cookieKeySession());
          tunnel.cookies().removeFromResponse(provider.cookieKeySignature());
        } else {
          
          if (trace != null) trace.trace("CP thbrejhby bytes != null");
          
          if (signatureCrypto != null) {
            byte[] signature = signatureCrypto.sign(bytes);
            final String signatureBase64 = bytesToBase64(signature);
            if (trace != null) trace.trace("CP tytgfyr save signature");
            tunnel.cookies().saveToResponse(provider.cookieKeySignature(), signatureBase64);
          }
          
          if (sessionCrypto != null) {
            if (trace != null) trace.trace("CP yfc56g34kjd sessionCrypto != null");
            bytes = sessionCrypto.encrypt(bytes);
          }
          
          final String bytesBase64 = bytesToBase64(bytes);
          if (trace != null) trace.trace("CP vv4t5v43t save bytesBase64");
          tunnel.cookies().saveToResponse(provider.cookieKeySession(), bytesBase64);
          
        }
        
      }
    };
    
    final HandlerKiller handlerKiller = tunnel.eventBeforeCompleteHeaders().addEventHandler(
        writeSessionToCookies);
    
    if (trace != null) trace.trace("CP bgeneju478r");
    
    try {
      
      if (!underSecurityUmbrella) {
        
        if (trace != null) trace.trace("CP nsbhryhfbv");
        
        whatWrapping.handleTunnel(tunnel);
        
        if (trace != null) trace.trace("CP jen4676v4g");
        
      } else {
        
        if (sessionStorage.getSessionBytes() == null) {
          if (trace != null) trace.trace("CP hytrjtreh redirect");
          tunnel.sendRedirect(provider.redirectOnSecurityError(target));
        } else {
          if (trace != null) trace.trace("CP fbdvfgd EXECUTE");
          whatWrapping.handleTunnel(tunnel);
        }
        
      }
      
    } finally {
      handlerKiller.kill();
      writeSessionToCookies.handle();
      if (trace != null) trace.trace("CP fjewhtb FINALLY");
    }
    
  }
}
