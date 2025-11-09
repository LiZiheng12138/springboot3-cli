Spring Security Authorization认证服务接入指南

当前模块是统一认证服务，如果业务系统需要接入该统一的认证服务，请按照如下步骤：

配置资源服务器需要resource/resourceServer目录下的文件
1. ResourceServerConfig.java 
2. SecurityAuthHandlers.java  
 这个两个是必须的，否则无法接入SSA的认证流程 直接复制到需要接入的资源服务
  
  
 如果资源服务不需要自定义的认证流程，则只需要配置这两个类即可  
 考虑到认证中心会接入多个资源服务，有的资源服务可能有各自的账号数据与权限数据，如果都走统一的认证流程显然不合适，那么需要自定义认证流程，可以添加配置如下类：  
 resource/resourceServer目录下的 BizJwtAuthenticationFilter.java 和 BizJwtService.java  
同时需要在ResourceServerConfig中添加一行代码：
   .addFilterBefore(bizJwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

  
  
 一个是自定义的认证过滤器，一个是自定义的token生成与解析，当然也可以换成你自己的认证授权的流程，本质上就是获取请求token判断是否合法，并解析token进行权限判断  
 如果没有ResourceServerConfig，实现一个/login接口，那么就可以不依赖于认证中心，自己实现认证流程。  
 所以有三种认证方式（案例是按照第2种实现）：
  1. 完全由认证中心实现，资源服务只负责提供数据不考虑认证与授权的实现  （只配置ResourceServerConfig、SecurityAuthHandlers）
  2. 登录认证在认证中心完成，用户的token由资源服务颁发，token的认证与授权也由资源服务实现 （实现第一步的同时，配置了自己的认证授权流程）
  3.  不接入认证中心，自己实现认证流程 （只配置自己的认证流程）
第1种方式可以实现统一的认证授权，但是有时候业务系统有自己的认证模式，那么第2种实现方式更适合登录在认证中心完成，具体的认证授权由各自服务实现，第3种方式适合单体应用，
接入认证中心可以实现一处登录，多处使用，并且token的获取设置都在后端完成，token无需经过前端更加安全。

SSA认证流程如下（第2种认证方式）：  
准备工作： 将上面需要的文件复制到对应的位置
然后需要在认证服务中注册客户端：
```java
 RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(clientId)
                        .clientSecret(passwordEncoder.encode("demo-secret"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://127.0.0.1:8789/user/auth/callback")
                        .redirectUri("http://localhost:8789/user/auth/callback")
                        .scope(OidcScopes.OPENID)
                        .scope("read")
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(30))
                                .refreshTokenTimeToLive(Duration.ofDays(30))
                                .reuseRefreshTokens(false) // rotation
                                .build())
                        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                        .build();
```
主要是client_id 客户端名称，client_secret 客户端密码，redirect_uri 回调地址，scope 资源权限，grant_type 授权模式  
注意：redirect_uri 回调地址可以是前端地址也可以是后端接口，认证中心登录完成之后会携带code到该地址，如果是后端接口那么你需要实现该接口。code是用来获取token的，code只能使用一次。
（本案例使用的是后端接口，建议使用后端接口，流程都在后端完成，前端只负责显示页面）
  
还需要在资源服务的数据库中需要添加资源服务的用户与认证中心的userId的映射，认证中心颁发的token解析后可以获取到唯一的userId，
资源服务需要将userId映射到自己的用户表以实现自定义的权限控制（不是第2种实现则不需要） 
  
  
登录流程：  
1. 前端访问接口 返回401 跳转到认证中心的授权页面 携带客户端参数请求/oauth2/authorize，若未登录则跳转到登录页面，已登录直接到第3步
2. 认证中心登录完成之后再次携带客户端参数请求/oauth2/authorize进行授权
3. 认证中心认证通过之后携带code到客户端对应的回调地址（这里是后端接口）
4. 回调地址拿到code请求/oauth2/token获取到认证中心颁发的token
5. 这里开始是自定义流程
6. 解析认证中心的token获取到认证中心的userId，根据userId查询当前系统的用户，若未查询到则自动注册
7. 根据当前系统的用户权限等信息生成token设置到请求当中,然后重定向到前端页面 至此登录流程结束
8. bizJwtAuthenticationFilter会获取每次请求中的token，解析后进行权限判断，token过期也会自动更新token
在认证中心登录完成之后，再访问接入了认证中心的其他资源服务器就无需再次登录。参考支付宝登录后，使用淘宝、饿了么等应用都可以直接使用支付宝账号登录，效果是类似的