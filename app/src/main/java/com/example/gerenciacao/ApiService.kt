package com.example.gerenciacao.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Todas as rotas da API do GerenciaCão usadas pelo app (Retrofit gera a implementação). */
interface ApiService {
    @POST("api/auth/cadastro") suspend fun cadastrar(@Body corpo: CadastroRequestDto): AuthResponseDto
    @POST("api/auth/login") suspend fun login(@Body corpo: LoginRequestDto): AuthResponseDto
    @POST("api/auth/aceitar-convite") suspend fun aceitarConvite(@Body corpo: AceitarConviteRequestDto): AuthResponseDto
    @GET("api/auth/eu") suspend fun eu(): UsuarioDto

    @GET("api/equipe") suspend fun listarEquipe(): List<UsuarioDto>
    @GET("api/equipe/{id}") suspend fun membro(@Path("id") id: String): UsuarioDto
    @POST("api/equipe") suspend fun convidar(@Body corpo: NovoMembroRequestDto): ConviteResponseDto
    @PUT("api/equipe/{id}") suspend fun atualizarMembro(@Path("id") id: String, @Body corpo: AtualizarMembroRequestDto): UsuarioDto
    @DELETE("api/equipe/{id}") suspend fun removerMembro(@Path("id") id: String)

    @GET("api/abrigo") suspend fun abrigo(): AbrigoDto
}
