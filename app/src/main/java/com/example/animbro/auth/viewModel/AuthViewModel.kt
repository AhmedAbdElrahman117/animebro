import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.animbro.repositories.AnimeRepositoryImp

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AnimeRepositoryImp
) : ViewModel() {

    fun sync() {
        viewModelScope.launch {
            repository.syncFromCloud()
        }
    }
}
