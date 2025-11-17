# Desafio Técnico Backend - Serasa

#### Esse projeto apresenta uma aplicação backend, desenvolvida em Java com SpringBoot e MySQL, para simular um sistema de transporte de grãos.


Ele está estruturado em:
- Cadastros: balanças, caminhões, filiais e tipos de grãos
- Processos: Início e fim de transações de transporte, pesagens dos caminhões
- Geração de relatórios

A estrutura está dividida em:
- Models: entidades do banco e DTO (data transfer objects)
- Repository: seguindo implementação padrão do Spring, com implementações para casos específicos de queries no banco 
- Services: Fila das pesagens dos caminhões em memória, serviços dos relatórios e validação de autenticação de balanças
- Controllers: Endpoints que expõem APIs REST para os domínios expostos acima (Cadastros, Processos e Geração de Relatórios)
- Utils: Tratador de exceção para retornar mensagens de erro amigáveis para o usuário

Adicionalmente, o projeto contém:
- Main->Resources->db->migration: Scripts de migration do banco
- Test->Resources->db->testData: dados sintéticos gerados para teste
- Test->Resources->PostmanTest: Collection do Postman para testes dos endpoints

## Execução
- Dependências
  - Docker
  - JDK 17.0
- O projeto pode ser executado via IDE (utilizei o IntelliJ) ou docker
  - Primeiramente, copiar o .env.example para .env e colocar senhas para o MySQL
  - Se não for rodar o projeto via docker, ainda sim executar o comando abaixo no console estando na pasta raiz:
      - ``` 
        docker compose -f compose.yaml up --build mysql
    - Isso é necessário para subir o banco e se o container não existir, criar o banco.
    
  - IDE: abrir o projeto e executar o src->main->java->com.serasa.DesafioTecnicoBackEnd->DesafioTecnicoBackEndApplication
  - Gradle: executar o comando abaixo no console, estando na pasta raiz:
    - ```
      ./gradlew clean bootRun
  - Docker: executar o comando abaixo no console, estando na pasta raiz:
    - ```
      docker compose -f compose.yaml up --build
  - O serviço estará acessível em http://localhost:8080, com documentação dos endpoints em http://localhost:8080/swagger-ui/index.html
  
## Fluxo de uma transação de transporte

#### Assumindo que os devidos cadastros estejam preenchidos, um processo de transação de transporte é feito seguindo as etapas a seguir:
- Uma transação de transporte é iniciada, sendo cada transação aberta vinculada a um caminhão e tipo de grão
  - Apenas uma transação de transporte para cada caminhão e tipo de grão pode estar aberta em determinado momento
  - Ao abrir uma transação de transporte, obtém-se o ID dela
  - A transação de transporte é aberta no momento que o caminhão sai da empresa em direção a fazenda

- Ao retornar, o caminhão deve registrar seu peso através do endpoint de pesagens
  - Cada balança possui uma autorização gerada em seu cadastro. Ela só poderá gerar a leitura caso informar essa autorização na requisição
  - O caminhão deverá ficar na balança no mínimo 1s, ou o tempo necessário para estabilizar o peso
  - São registradas até as últimas 10 leituras para aquele caminhão e balança, com intervalo mínimo de 100ms entre elas (tempo que o ESP32 envia esses dados)
    - Se uma leitura for enviada antes dos 100ms, sua leitura é descartada
  - Para considerar o peso como estável, são consideradas três regras:
    - Houveram no mínimo 10 leituras para essa balança
    - A diferença entre o peso máximo e mínimo é de no máximo 5%, a fim de descartar oscilações maiores.
    - As leituras estão dentro de 2,5% da mediana das leituras
  - Caso o peso seja considerado estável, é retornada a última leitura
  
- Finalizado o registro do peso (que é mantido em memória por até 5 minutos desde a última leitura), deve-se finalizar a transação de transporte
  - Isso é feito passando o ID da transação de transporte e o ID da pesagem ao endpoint de finalizar transação de transporte
  - A transação é finalizada apenas caso a pesagem esteja estável, do contrário a chamada falha
  - Ao estar estável, a pesagem é removida da memória e sua leitura gravada e vinculada à transação de transporte sendo finalizada

## Relatórios disponíveis
- Pesagens efetuadas por filial, caminhão, tipo de grão e período
- Custos de compra por filial, caminhão, tipo de grão e período
- Lucros possíveis por filial, caminhão, tipo de grão e período
  - O cálculo do lucro é baseado na quantidade de grãos em estoque
  - O lucro vai de 5% a 20%, sendo maior quanto menos grãos em estoque
    - O lucro diminui 0,5% a cada 0,5 tonelada.
    - O lucro não será menor do que 5%.
- Os endpoints de relatórios exigem todos os parâmetros como filtros.

## Sugestões de melhoria
Essa é uma versão inicial, portanto há diversas melhorias que podem serem feitas:
- HTTPS: Normalmente visto no deploy, pela equipe de DevOps. Colocaria um NGinx para cuidar da parte de HTTPS, pois não sei como é a performance de tratar isso no Java. Na minha experiência com node, tratar o HTTPS via NGinx foi muito mais performático e separa a responsabilidade do serviço. 
- Autenticação: autenticar as requisições com JWT ou API Key, para limitar quem possui acesso aos endpoints de cadastro e operações específicas.
- Observabilidade e logs mais detalhados para os endpoints.
- Paginação de retorno de dados, especialmente nos endpoints que retornam todos os cadastros e nos relatórios.
- Endpoints de cadastro: não retornar as entidades completas a não ser que explicitamente necessário, fazer DTOs específicos para retorno dos dados
  - Exemplo: Balança. A autorização é gerada no momento do cadastro e poderia ser tratada como um Secret/API Key, não retornando em requisições posteriores de busca das balanças.
- Pesagens: os dados são armazenados em memória. 
  - Caso seja necessário acesso a mais de uma instância do serviço, por conta de escalonamento por questões de carga ou limitações de memória, é interessante uma solução com Redis.
  - Adicionalmente, pode ser interessante incluir uma fila para receber as pesagens, por conta da possível volumetria.
  - Adicionei a geração de um ID em memória para o registro (sessão) da pesagem, mas não forçado seu uso nas requisições posteriores. Utilizar esse registro melhoraria a performance pois não precisaria fazer buscas adicionais.
- Nos relatórios:
  - Adicionar opções de filtros mais flexíveis.
    - Devemos apenas cuidar para garantir algum filtro mínimo (ex.: por filial, por caminhão)
    - Indexar todas as colunas das tabelas envolvidas para cada campo de filtro é custoso, especialmente para as gravações. 
  - Adicionar uma fila para o relatório, para processar eles assincronamente. Os relatórios podem ser um processo pesado dependendo do volume de dados envolvido.
- Adicionar configurações para o usuário definir os critérios de estabilização da balança e variações aceitas, bem como o critério para cálculo de lucro nos relatórios.
- Adicionar os processos de saídas dos grãos, para representar corretamente o estoque de grãos real na doca. 
- Efetuar testes de performance, especialmente com várias leituras simultâneas.