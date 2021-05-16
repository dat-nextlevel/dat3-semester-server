package facades;

import com.google.gson.Gson;
import dtos.*;
import threads.MyCallable;
import java.util.concurrent.*;

public class ExternalFetchFacade {
    String chuckNorrisUrl = "https://api.chucknorris.io/jokes/random";
    String dadJokeUrl = "https://icanhazdadjoke.com/";
    String animeQuoteUrl = "https://animechan.vercel.app/api/random";
    String tronaldDumpUrl = "https://api.tronalddump.io/random/quote";
    String jeopardyUrl = "https://jservice.io/api/random";

    Gson GSON = new Gson();

    public ChuckNorrisJokeDTO getChuckNorrisJoke(String data) {
        return GSON.fromJson(data, ChuckNorrisJokeDTO.class);
    }

    public DadJokeDto getDadJoke(String data) {
        return GSON.fromJson(data, DadJokeDto.class);
    }

    public AnimeQuoteDTO getAnimeQuote(String data) {
        return GSON.fromJson(data, AnimeQuoteDTO.class);
    }

    public TronaldDumpDTO getTronaldDumpQuote(String data) {
        TronaldDumpDTO tronaldDumpDTO = GSON.fromJson(data, TronaldDumpDTO.class);
        tronaldDumpDTO.setHref();
        return tronaldDumpDTO;
    }

    public JeopardyDTO getJeopardyQuestion(String data) {
        return GSON.fromJson(data.replace("[", "").replace("]", ""), JeopardyDTO.class);
    }

    public CombinedDTO getCombinedDTO() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        //long startTime = System.nanoTime();

        Callable<String> getChuckNorris = new MyCallable(chuckNorrisUrl);
        Callable<String> getDadJoke = new MyCallable(dadJokeUrl);
        Callable<String> getAnimeQuote = new MyCallable(animeQuoteUrl);
        Callable<String> getTronaldDump = new MyCallable(tronaldDumpUrl);
        Callable<String> getJeopardy = new MyCallable(jeopardyUrl);

        Future<String> future = executor.submit(getChuckNorris);
        Future<String> future1 = executor.submit(getDadJoke);
        Future<String> future2 = executor.submit(getAnimeQuote);
        Future<String> future3 = executor.submit(getTronaldDump);
        Future<String> future4 = executor.submit(getJeopardy);

        ChuckNorrisJokeDTO chuckNorrisJokeDTO = getChuckNorrisJoke(future.get());
        DadJokeDto dadJokeDTO = getDadJoke(future1.get());
        AnimeQuoteDTO animeQuoteDTO = getAnimeQuote(future2.get());
        TronaldDumpDTO tronaldDumpDTO = getTronaldDumpQuote(future3.get());
        JeopardyDTO jeopardyDTO = getJeopardyQuestion(future4.get());

        CombinedDTO combinedDTO = new CombinedDTO(chuckNorrisJokeDTO, dadJokeDTO, animeQuoteDTO, tronaldDumpDTO, jeopardyDTO);
        /*long elapsedTime = System.nanoTime() - startTime;
        System.out.println(elapsedTime / 1000000);*/
        executor.shutdown();
        return combinedDTO;
    }
}