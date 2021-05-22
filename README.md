# datasul-ldap-tester
Para testar as configurações do serviço de domínio que será utilizado para autenticar os usuários, na nova versão do Datatsul, DTS4THF, pode-se utilizar o utilitário de linha de comando datasul-ldap-tester, desenvolvido para esta finalidade.

## Requisitos:

- Java Runtime Enviroment 8

### Guia passo a passo:

* Configurar o acesso ao servidor de domínio utilizando um dos arquivos de propriedades listados a seguir:
    * application.base.properties
       * ldap.url: URL do serviço de domínio
       * ldap.user.base: Lista de nomes (Distinguished Names) para busca na árvore de informações de diretório (DIT)
    * application.search.properties
       * ldap.url: URL do serviço de domínio
       * ldap.manager.dn: Lista de nomes (Distinguished Names) para busca na árvore de informações de diretório (DIT) 
       * ldap.manager.password: Senha do usuário genérico, no formato BASE64, utilizado para acessar o serviço de domínio
       * ldap.user.search.base: Informar os diretórios de busca no serviço de domínio
* Executar, via linha de comando o comando: java -jar -Dspring.config.location=arquivo datasul-ldap-tester-1.0.jar informando o caminho completo para um dos arquivos de propriedades previamente configurados
* Informar o usuário e senha

### Possíveis saídas:
* LDAP Authentication result: Success
* LDAP Authentication result: Fail

***
Todas as mensagens mostradas no console iniciam com LDAP.
