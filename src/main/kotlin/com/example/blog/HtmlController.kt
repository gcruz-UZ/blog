package com.example.blog

import format
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.server.ResponseStatusException

// MVC con plantillas mustache
@Controller
class HtmlController(private val repository: ArticleRepository,
                     private val properties: BlogProperties) {

    @GetMapping("/")
    fun blog(model: Model): String {
        model["title"] = properties.title
        model["banner"] = properties.banner
        //conversion de modelo. en it.render esta llamando a linea 37
        model["articles"] = repository.findAllByOrderByAddedAtDesc().map { it.render() }
        return "blog" //id plantilla (vista)
    }

    @GetMapping("/article/{slug}")
    fun article(@PathVariable slug: String, model: Model): String {
        val article = repository
                .findBySlug(slug)
                ?.render()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This article does not exist")
        model["title"] = article.title
        model["article"] = article
        return "article" //id plantilla moustache
    }

    //modelo vista cliente
    fun Article.render() = RenderedArticle(
            slug,
            title,
            headline,
            content,
            author,
            addedAt.format()
    )

    data class RenderedArticle(
            val slug: String,
            val title: String,
            val headline: String,
            val content: String,
            val author: User,
            val addedAt: String)

}