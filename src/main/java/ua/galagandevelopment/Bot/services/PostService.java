package ua.galagandevelopment.Bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.galagandevelopment.Bot.models.Post;
import ua.galagandevelopment.Bot.repositories.PostRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    public Post savePost(Post post) {
        return postRepository.save(post);
    }
    public Post getPostByTest(String text) {return postRepository.findByTitle(text);}
    public List<Post> getAllPosts() {return postRepository.findAll();}
    public Post getPostById(int id) {return postRepository.findById(id);}
}
