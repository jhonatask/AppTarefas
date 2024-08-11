# Apptarefas

## Descrição
Aplicação de gerenciamento de tarefas utilizando Spring Boot 3, MongoDB, Redis, OAuth2, e AWS.
Utilizando de springwebflux para a criação de um sistema reativo, com a utilização de MongoDB para persistência de dados,
Redis para cache, OAuth2 para autenticação e autorização, AWS S3 para armazenamento de arquivos e AWS Lambda para processamento de eventos.

## Configuração
### Java
- **Versão:** 17

### Banco de Dados
- **MongoDB:** 4.4.9
- **Redis:** 6.2.6

### AWS
- **AWS S3:** Bucket criado na região us-east-1
- **AWS S3:** Credenciais configuradas no arquivo `application.properties`


**Criar seu bucket e configurar as credenciais da aws no arquivo application.properties**


## AWS LAMBDA
- **AWS LAMBDA:** Criar uma função lambda e configurar o trigger para o S3
```java
public class LambdaHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event event, Context context) {
        event.getRecords().forEach(record -> {
            String taskId = record.getS3().getObject().getKey();
            String bucketName = record.getS3().getBucket().getName();

            System.out.println("Tarefa criada com sucesso id: " + record.getS3().getObject().getKey());
        });
        return "Tarefa Criado com sucesso" + event.getRecords().get(0).getS3().getObject().getKey();
    }
}
```

### Pré-requisitos
- Docker
- Docker Compose

### Executando a Aplicação ( ambiente de desenvolvimento)

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/apptarefas.git
   cd apptarefas

2. Construa e inicie os containers Docker:  necessario ter o docker e docker-compose instalado
    ```bash
   docker-compose up --build
   
Acesse a aplicação em http://localhost:8080.

3. A documentação da API pode ser acessada em http://localhost:8080/swagger-ui.html.

4. Para parar a execução dos containers, utilize o comando:
    ```bash
   docker-compose down
   
5. Ao subir a aplicação, o banco de dados é populado com um usuário padrão:
    - **Usuário:** admin
    - **Senha:** Pamonha123*
6. Para realizar a chamadas das requisições é necessário enviar o token gerado no endpoint de login.

