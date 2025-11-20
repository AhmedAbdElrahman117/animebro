package com.example.animbro.data.remote

import com.example.animbro.data.remote.dto.AnimeListResponseDTO
import com.example.animbro.data.remote.dto.AnimeNodeDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Endpoints {
    @GET("v2/anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10,
        @Query("nsfw") nsfw: Boolean = false,
        @Query("fields") fields: String = "id,title,main_picture,start_date,end_date,synopsis,rank,popularity,num_scoring_users,status,genres,num_episodes,average_episode_duration,rating,pictures,recommendations,videos"
    ): Response<AnimeListResponseDTO>

    @GET("v2/anime/{anime_id}")
    suspend fun getAnimeDetails(
        @Path("anime_id") animeId: Int,
        @Query("fields") fields: String = "id,title,main_picture,start_date,end_date,synopsis,rank,popularity,num_scoring_users,status,genres,num_episodes,average_episode_duration,rating,pictures,recommendations,videos"
    ): Response<AnimeNodeDTO>

    @GET("v2/anime/ranking")
    suspend fun getAnimeRanking(
        @Query("ranking_type") rankingType: String = "all",
        @Query("limit") limit: Int = 10,
        @Query("fields") fields: String = "id,title,main_picture,start_date,end_date,synopsis,rank,popularity,num_scoring_users,status,genres,num_episodes,average_episode_duration,rating,pictures,recommendations,videos",
        @Query("nsfw") nsfw: Boolean = false
    ): Response<AnimeListResponseDTO>

}

// always query parameter, nsfw = false

// using client id
// https://api.myanimelist.net/v2/anime?q=one&limit=4
// https://api.myanimelist.net/v2/anime/30230?fields=id,title,main_picture,alternative_titles,start_date,end_date,synopsis,mean,rank,popularity,num_list_users,num_scoring_users,nsfw,created_at,updated_at,media_type,status,genres,my_list_status,num_episodes,start_season,broadcast,source,average_episode_duration,rating,pictures,background,related_anime,related_manga,recommendations,studios,statistics,videos
// https://api.myanimelist.net/v2/anime/ranking?ranking_type=all&limit=4

// use token for specific user
// https://api.myanimelist.net/v2/anime/suggestions?limit=4?nsfw=false